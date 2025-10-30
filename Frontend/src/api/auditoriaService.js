import axios from "axios";

const auditoriaApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/auditoria/logs",
  withCredentials: true,
});

auditoriaApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const buscarLogs = async (params) => {
  const res = await auditoriaApi.get("/buscar", { params });
  return res.data;
};

export const exportCsvBackend = async (params = {}) => {
  const toIso = (v) => {
    if (!v) return undefined;
    const d = new Date(v);
    if (isNaN(d.getTime())) return undefined;
    return d.toISOString();
  };

  const q = {};
  if (params.eventCode) q.eventCode = params.eventCode;
  if (params.userId) q.userId = params.userId;
  const fromIso = toIso(params.from);
  const toIsoStr = toIso(params.to);
  if (fromIso) q.from = fromIso;
  if (toIsoStr) q.to = toIsoStr;

  const res = await auditoriaApi.get("/export", {
    params: q,
    responseType: "blob",
  });

  const dispo = res.headers?.["content-disposition"] || "";
  const m =
    /filename\*?=(?:UTF-8''|")?([^\";]+)"/i.exec(dispo) ||
    /filename=([^;]+)/i.exec(dispo);
  const filename = m
    ? decodeURIComponent(m[1].replace(/"/g, ""))
    : `auditoria-${new Date()
        .toISOString()
        .slice(0, 19)
        .replace(/[:T]/g, "-")}.csv`;

  const blobUrl = URL.createObjectURL(res.data);
  const a = document.createElement("a");
  a.href = blobUrl;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(blobUrl);
};

export default auditoriaApi;
