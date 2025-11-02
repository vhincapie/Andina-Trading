import consolidacionApi from "./axiosConsolidacion";
import axios from "axios";

const downloadFromResponse = (res, fallbackName) => {
  const dispo = res.headers?.["content-disposition"] || "";
  const m =
    /filename\*?=(?:UTF-8''|")?([^\";]+)"/i.exec(dispo) ||
    /filename=([^;]+)/i.exec(dispo);
  const filename = m
    ? decodeURIComponent((m[1] || "").replace(/"/g, ""))
    : fallbackName;

  const url = URL.createObjectURL(res.data);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);

  return { filename };
};

export const descargarRegionalCsv = async () => {
  const res = await consolidacionApi.get("/regional/csv", {
    responseType: "blob",
  });
  return downloadFromResponse(
    res,
    `consolidacion_regional-${new Date().toISOString().slice(0, 19).replace(/[:T]/g, "-")}.csv`
  );
};


export const descargarSegmentacionCsv = async (params = {}) => {
  const res = await consolidacionApi.get("/segmentacion/csv", {
    params,
    responseType: "blob",
  });
  return downloadFromResponse(
    res,
    `segmentacion_inversionistas-${new Date().toISOString().slice(0, 19).replace(/[:T]/g, "-")}.csv`
  );
};


export const descargarComisionesCsv = async () => {
  const res = await consolidacionApi.get("/comisiones/csv", {
    responseType: "blob",
  });
  return downloadFromResponse(
    res,
    `consolidacion_comisiones-${new Date().toISOString().slice(0, 19).replace(/[:T]/g, "-")}.csv`
  );
};


export const listarPaises = async () => {
  const api = axios.create({
    baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/catalogos/paises",
    withCredentials: true,
  });
  api.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });
  const res = await api.get("/listar");
  return res.data || [];
};
