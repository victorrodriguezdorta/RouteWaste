/**
 * Generates rest-api_tests.json and rest-api_environment.json for Newman.
 * Targets the api-test back-end (http://localhost:8081, MongoDB db-application-api-test).
 * Run: node scripts/generate-rest-api-postman.mjs
 */
import { writeFileSync } from "fs";
import { randomUUID } from "crypto";
import { join, dirname } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const root = join(__dirname, "..");

const TEST_STATUS_200 = [
  'pm.test("Status is 200", function () {',
  "    pm.response.to.have.status(200);",
  "});",
];

const TEST_STATUS_201 = [
  'pm.test("Status is 201", function () {',
  "    pm.response.to.have.status(201);",
  "});",
];

const TEST_STATUS_400 = [
  'pm.test("Status is 400", function () {',
  "    pm.response.to.have.status(400);",
  "});",
];

const TEST_STATUS_404 = [
  'pm.test("Status is 404", function () {',
  "    pm.response.to.have.status(404);",
  "});",
];

const TEST_PAGE = [
  'pm.test("Status is 200", function () {',
  "    pm.response.to.have.status(200);",
  "});",
  "const body = pm.response.json();",
  'pm.test("Paginated response shape", function () {',
  '    pm.expect(body).to.have.property("content");',
  "    pm.expect(body.content).to.be.an(\"array\");",
  '    pm.expect(body).to.have.property("totalElements");',
  "});",
];

const TEST_OVERVIEW = [
  ...TEST_STATUS_200,
  "const body = pm.response.json();",
  'pm.test("Overview response shape", function () {',
  '    pm.expect(body).to.have.property("entityCounts");',
  '    pm.expect(body).to.have.property("recentInfrastructurePlans");',
  "    pm.expect(body.recentInfrastructurePlans).to.be.an(\"array\");",
  "    pm.expect(body.entityCounts).to.have.property(\"containers\");",
  "    pm.expect(body.entityCounts).to.have.property(\"vehicles\");",
  "    pm.expect(body.entityCounts).to.have.property(\"facilities\");",
  "    pm.expect(body.entityCounts).to.have.property(\"infrastructurePlans\");",
  "});",
];

function requireIdPre(idVar, resourceLabel) {
  return [
    `const entityId = pm.environment.get("${idVar}");`,
    "if (!entityId) {",
    `    throw new Error("${resourceLabel} id is not set. Run the POST create request in this folder first.");`,
    "}",
  ];
}

function createResourceTests(idVar) {
  return [
    'pm.test("Status is 201", function () {',
    "    pm.response.to.have.status(201);",
    "});",
    "const created = pm.response.json();",
    `if (created.id) {`,
    `    pm.environment.set("${idVar}", created.id);`,
    "}",
    'pm.test("Created resource has id and name", function () {',
    "    pm.expect(created).to.have.property(\"id\");",
    "    pm.expect(created).to.have.property(\"name\");",
    "});",
  ];
}

function createRequest(name, method, path, options = {}) {
  const {
    tests = TEST_STATUS_200,
    prerequest = [],
    body = null,
    description = "",
  } = options;

  const urlPath = path.startsWith("/") ? path : `/${path}`;

  const events = [];
  if (prerequest.length) {
    events.push({
      listen: "prerequest",
      script: { type: "text/javascript", exec: prerequest },
    });
  }
  if (tests.length) {
    events.push({
      listen: "test",
      script: { type: "text/javascript", exec: tests },
    });
  }

  const request = {
    method,
    header: [{ key: "Content-Type", value: "application/json" }],
    url: `{{baseUrl}}${urlPath}`,
    description,
  };

  if (body) {
    request.body = { mode: "raw", raw: JSON.stringify(body, null, 2) };
  }

  return { name, event: events, request };
}

function crudFolder(resourceName, basePath, postBody, putBody, idVar) {
  const listPath = `${basePath}/`;
  const byIdPath = `${basePath}/{{${idVar}}}`;
  const requireId = requireIdPre(idVar, resourceName);

  const createTests = createResourceTests(idVar);

  const getByIdTests = [
    'pm.test("Status is 200", function () {',
    "    pm.response.to.have.status(200);",
    "});",
    "const resource = pm.response.json();",
    'pm.test("Resource has id", function () {',
    "    pm.expect(resource).to.have.property(\"id\");",
    `    pm.expect(resource.id).to.eql(pm.environment.get("${idVar}"));`,
    "});",
  ];

  const updateTests = [
    ...TEST_STATUS_200,
    "const json = pm.response.json();",
    'pm.test("Updated resource has id", function () {',
    "    pm.expect(json).to.have.property(\"id\");",
    "});",
  ];

  const deleteTests = [
    ...TEST_STATUS_200,
    "const json = pm.response.json();",
    'pm.test("Deleted resource returns id", function () {',
    "    pm.expect(json).to.have.property(\"id\");",
    "});",
  ];

  return {
    name: resourceName,
    item: [
      createRequest(`GET ${resourceName} list`, "GET", listPath, {
        tests: TEST_PAGE,
        description: `List ${resourceName} with pagination`,
      }),
      createRequest(`POST Create ${resourceName}`, "POST", listPath, {
        tests: createTests,
        body: postBody,
      }),
      createRequest(`GET ${resourceName} by id`, "GET", byIdPath, {
        prerequest: requireId,
        tests: getByIdTests,
      }),
      createRequest(`PUT Update ${resourceName}`, "PUT", byIdPath, {
        prerequest: requireId,
        tests: updateTests,
        body: putBody,
      }),
      createRequest(`DELETE ${resourceName}`, "DELETE", byIdPath, {
        prerequest: requireId,
        tests: deleteTests,
      }),
      createRequest(`GET ${resourceName} by id (invalid uuid)`, "GET", `${basePath}/not-a-uuid`, {
        tests: TEST_STATUS_400,
        description: "Validation error for malformed id",
      }),
      createRequest(`GET ${resourceName} by id (not found)`, "GET", `${basePath}/00000000-0000-0000-0000-000000000001`, {
        tests: TEST_STATUS_404,
      }),
    ],
  };
}

const vehiclePost = {
  name: "Newman Test Vehicle {{$timestamp}}",
  vehicleType: "COLLECTION_TRUCK",
  capacityKilograms: 5000,
  CapacityLiters: 12000,
  costPerKilometer: { amount: 0.45, currency: "EUR" },
};

const vehiclePut = {
  name: "Newman Test Vehicle Updated {{$timestamp}}",
  vehicleType: "COLLECTION_TRUCK",
  capacityKilograms: 5500,
  CapacityLiters: 13000,
  costPerKilometer: { amount: 0.5, currency: "EUR" },
};

const containerPost = {
  name: "Newman Test Container {{$timestamp}}",
  location: {
    latitude: 28.4636,
    longitude: -16.2518,
    postalAddress: "Calle Test 1",
    gisReference: "GIS-NEWMAN-TEST",
  },
  wasteType: "ORGANIC",
  capacityLiters: { liters: 1000 },
  dailyDemandLitersPerDay: { litersPerDay: 80 },
  serviceZone: "NEIGHBORHOOD",
};

const containerPut = {
  name: "Newman Test Container Updated {{$timestamp}}",
  location: {
    latitude: 28.464,
    longitude: -16.252,
    postalAddress: "Calle Test 2",
    gisReference: "GIS-NEWMAN-TEST-UPD",
  },
  wasteType: "ORGANIC",
  capacityLiters: { liters: 1100 },
  dailyDemandLitersPerDay: { litersPerDay: 90 },
  serviceZone: "NEIGHBORHOOD",
};

const facilityPost = {
  name: "Newman Test Facility {{$timestamp}}",
  facilityType: "TRANSFER_STATION",
  location: {
    latitude: 28.47,
    longitude: -16.25,
    postalAddress: "Avenida Test 10",
    gisReference: "GIS-FACILITY-NEWMAN",
  },
  storageCapacity: { value: 10000 },
  processingCapacity: { value: 5000 },
  unloadingTime: { timeValue: 30 },
  openingFixedCost: { amount: 1500, currency: "EUR" },
  status: "CANDIDATE",
};

const facilityPut = {
  name: "Newman Test Facility Updated {{$timestamp}}",
  facilityType: "TRANSFER_STATION",
  location: {
    latitude: 28.471,
    longitude: -16.251,
    postalAddress: "Avenida Test 11",
    gisReference: "GIS-FACILITY-NEWMAN-UPD",
  },
  storageCapacity: { value: 11000 },
  processingCapacity: { value: 5500 },
  unloadingTime: { timeValue: 35 },
  openingFixedCost: { amount: 1600, currency: "EUR" },
  status: "CANDIDATE",
};

const infrastructureListTests = [
  ...TEST_PAGE,
  "const plansPage = pm.response.json();",
  "if (plansPage.content && plansPage.content.length > 0 && plansPage.content[0].id) {",
  "    pm.environment.set('infrastructurePlanId', plansPage.content[0].id);",
  "}",
];

const skipIfNoPlanPre = [
  "if (!pm.environment.get('infrastructurePlanId')) {",
  "    pm.execution.skipRequest();",
  "}",
];

const infrastructureGetByIdTests = [
  'pm.test("Status is 200", function () {',
  "    pm.response.to.have.status(200);",
  "});",
  "const plan = pm.response.json();",
  'pm.test("Plan has id", function () {',
  "    pm.expect(plan).to.have.property(\"id\");",
  "});",
];

const collection = {
  info: {
    _postman_id: randomUUID(),
    name: "Sensor App REST API",
    description:
      "Automated REST API tests for sensor-app back-end. Generated by scripts/generate-rest-api-postman.mjs",
    schema: "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
  },
  variable: [{ key: "baseUrl", value: "http://localhost:8081" }],
  item: [
    {
      name: "Application Overview",
      item: [
        createRequest("GET Application Overview", "GET", "/api/v1/application-overview", {
          tests: TEST_OVERVIEW,
        }),
      ],
    },
    crudFolder("Vehicles", "/api/v1/vehicles", vehiclePost, vehiclePut, "vehicleId"),
    crudFolder("Containers", "/api/v1/containers", containerPost, containerPut, "containerId"),
    crudFolder("Facilities", "/api/v1/facilities", facilityPost, facilityPut, "facilityId"),
    {
      name: "Infrastructure Plans",
      item: [
        createRequest("GET Infrastructure Plans list", "GET", "/api/v1/infrastructure-plans", {
          tests: infrastructureListTests,
        }),
        createRequest("GET Infrastructure Plans list (invalid page)", "GET", "/api/v1/infrastructure-plans?page=-1&size=10", {
          tests: TEST_STATUS_400,
        }),
        createRequest("GET Infrastructure Plan by id", "GET", "/api/v1/infrastructure-plans/{{infrastructurePlanId}}", {
          prerequest: skipIfNoPlanPre,
          tests: infrastructureGetByIdTests,
          description: "Runs only when the list request found at least one plan in the database",
        }),
        createRequest("GET Infrastructure Plan by id (not found)", "GET", "/api/v1/infrastructure-plans/00000000-0000-0000-0000-000000000001", {
          tests: TEST_STATUS_404,
        }),
        createRequest("DELETE Infrastructure Plan (not found)", "DELETE", "/api/v1/infrastructure-plans/00000000-0000-0000-0000-000000000002", {
          tests: TEST_STATUS_404,
          description: "Safe delete test without removing existing plans from the database",
        }),
      ],
    },
    {
      name: "Algorithms",
      item: [
        createRequest("POST Execute Algorithm (validation error)", "POST", "/api/v1/algorithms/execute", {
          tests: TEST_STATUS_400,
          body: {},
          description: "Empty body triggers validation error without running Docker algorithm",
        }),
      ],
    },
  ],
};

const environment = {
  id: randomUUID(),
  name: "Sensor App API Test",
  values: [
    { key: "baseUrl", value: "http://localhost:8081", type: "default", enabled: true },
    { key: "vehicleId", value: "", type: "default", enabled: true },
    { key: "containerId", value: "", type: "default", enabled: true },
    { key: "facilityId", value: "", type: "default", enabled: true },
    { key: "infrastructurePlanId", value: "", type: "default", enabled: true },
  ],
  _postman_variable_scope: "environment",
  _postman_exported_at: new Date().toISOString(),
  _postman_exported_using: "generate-rest-api-postman.mjs",
};

writeFileSync(join(root, "rest-api_tests.json"), JSON.stringify(collection, null, 2));
writeFileSync(join(root, "rest-api_environment.json"), JSON.stringify(environment, null, 2));

console.log("Written rest-api_tests.json and rest-api_environment.json");
