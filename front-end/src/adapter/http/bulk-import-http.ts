/**
 * Uploads a JSON file to a bulk import endpoint using multipart form data.
 *
 * @param url full URL of {@code .../bulk/import}
 * @param file selected JSON file
 */
export function postBulkImportFile(url: string, file: File): Promise<Response> {
  const formData = new FormData();
  formData.append('file', file);
  return fetch(url, {
    method: 'POST',
    body: formData,
  });
}
