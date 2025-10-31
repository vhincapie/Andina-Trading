import backupApi from "./axiosBackup";

const pickArray = (payload) => {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.data)) return payload.data;
  if (Array.isArray(payload?.files)) return payload.files;
  if (Array.isArray(payload?.archivos)) return payload.archivos;
  return [];
};

export const listarRespaldoArchivos = async () => {
  const res = await backupApi.get("/archivos");
  return pickArray(res.data);
};

export const ejecutarBackupDb = async (db) => {
  const res = await backupApi.post(`/ejecutar/${encodeURIComponent(db)}`);
  return res.data;
};

export const ejecutarBackupTodo = async () => {
  const res = await backupApi.post("/ejecutar-todo");
  return pickArray(res.data);
};

export const crearZip = async (files) => {
  const res = await backupApi.post("/zip", files);
  return res.data;
};

export const descargarArchivo = async (filename) => {
  const res = await backupApi.get(
    `/descargar/${encodeURIComponent(filename)}`,
    {
      responseType: "blob",
    }
  );
  return { data: res.data, filename };
};

export const restaurarDb = async (db, filename) => {
  const res = await backupApi.post(
    `/restaurar/${encodeURIComponent(db)}?filename=${encodeURIComponent(
      filename
    )}`
  );
  return res.data;
};
