#!/usr/bin/env bash
set -euo pipefail

# Renombra directorios con CamelCase a kebab-case bajo front-end/src
# y actualiza referencias en imports (.ts, .vue, .js).
# Uso:
#   ./rename-folders-to-kebab.sh [--dry-run]

BASE_DIR="front-end/src"
DRY_RUN=false

if [ "$#" -gt 0 ] && [ "$1" = "--dry-run" ]; then
  DRY_RUN=true
fi

to_kebab() {
  local name="$1"
  # Insert hyphen between camel transitions and between uppercase sequences followed by lowercase
  echo "$name" | sed -E 's/([a-z0-9])([A-Z])/'"\1-\2"'/g; s/([A-Z])([A-Z][a-z])/'"\1-\2"'/g' | tr '[:upper:]' '[:lower:]'
}

echo "🔎 Buscando directorios CamelCase en: $BASE_DIR"
cd "$(dirname "$0")/.." || exit 1
cd "$BASE_DIR" || { echo "Directorio $BASE_DIR no encontrado"; exit 1; }

# Recolectar directorios con mayúsculas (lista bottom-up para renombrar hijos antes que padres)
mapfile -t DIRS < <(find . -depth -type d -name '*[A-Z]*' -print)

if [ ${#DIRS[@]} -eq 0 ]; then
  echo "✅ No se encontraron carpetas CamelCase en $BASE_DIR"
  exit 0
fi

declare -a RENAMES_OLD
declare -a RENAMES_NEW

for d in "${DIRS[@]}"; do
  # eliminar prefijo ./
  rel="${d#./}"
  parent_dir=$(dirname "$rel")
  base_name=$(basename "$rel")
  new_name=$(to_kebab "$base_name")
  if [ "$base_name" = "$new_name" ]; then
    continue
  fi
  old_path="$parent_dir/$base_name"
  new_path="$parent_dir/$new_name"
  # Normalizar path sin ./
  old_path="${old_path#./}"
  new_path="${new_path#./}"
  RENAMES_OLD+=("$old_path")
  RENAMES_NEW+=("$new_path")
done

echo "Found ${#RENAMES_OLD[@]} directories to rename. Processing bottom-up..."

for i in "${!RENAMES_OLD[@]}"; do
  old="${RENAMES_OLD[$i]}"
  new="${RENAMES_NEW[$i]}"
  if [ -d "$old" ]; then
    if [ -e "$new" ]; then
      echo "⚠ Target exists, skipping: $old -> $new"
      continue
    fi
    if $DRY_RUN; then
      echo "DRY: would rename: $old -> $new"
    else
      # Crear directorio padre del target si no existe
      mkdir -p "$(dirname "$new")"
      if git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
        git mv "$old" "$new" || mv "$old" "$new"
      else
        mv "$old" "$new"
      fi
      echo "✓ Renombrado: $old -> $new"
    fi
  else
    echo "⚠ No existe (ya renombrado?): $old"
  fi
done

echo "\n🔁 Actualizando imports en archivos (.ts, .vue, .js)..."
if $DRY_RUN; then
  echo "DRY: no se modificarán archivos, solo mostraría cambios potenciales"
fi

# Construir expresiones de reemplazo seguras (solo reemplaza entre separadores de path)
for i in "${!RENAMES_OLD[@]}"; do
  old="${RENAMES_OLD[$i]}"
  new="${RENAMES_NEW[$i]}"
  if $DRY_RUN; then
    echo "DRY: would replace '/$old/' -> '/$new/' in source files"
  else
    # Reemplazar rutas en importaciones y require
    # Maneja comillas simples y dobles
    grep -rl --exclude-dir=node_modules --exclude-dir=.git "/$old/" . --include='*.ts' --include='*.vue' --include='*.js' 2>/dev/null | while IFS= read -r file; do
      sed -i "s|/$old/|/$new/|g" "$file" || true
      sed -i "s|./$old/|./$new/|g" "$file" || true
      sed -i "s|'./$old/|'./$new/|g" "$file" || true
      sed -i "s|\"./$old/|\"./$new/|g" "$file" || true
      echo "  ✓ Actualizado: $file"
    done
  fi
done

echo "\n✅ Hecho. Resumen:"
echo "  - Directorios renombrados: ${#RENAMES_OLD[@]}"
if $DRY_RUN; then
  echo "  - Modo: DRY RUN (no se aplicaron cambios)"
else
  echo "  - Ejecuta 'git status' y prueba la construcción del front-end"
fi

exit 0
