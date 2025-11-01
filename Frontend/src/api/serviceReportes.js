import reportesApi from "./axiosReportes";

const filenameFromDisposition = (res, fallback) => {
  const cd = res?.headers?.["content-disposition"] || res?.headers?.["Content-Disposition"];
  if (!cd) return fallback;
  const m = /filename\*?=(?:UTF-8'')?["']?([^"';]+)["']?/i.exec(cd);
  if (m && m[1]) return decodeURIComponent(m[1]);
  return fallback;
};

const triggerDownload = (blob, filename = "reporte.csv") => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
};

const getCsv = async (path, defaultName) => {
  try {
    const res = await reportesApi.get(path, {
      responseType: "blob",
    });
    const filename = filenameFromDisposition(res, defaultName);
    triggerDownload(res.data, filename);
    return { ok: true, filename };
  } catch (e) {
    const msg =
      e?.response?.data?.message ||
      e?.message ||
      "No se pudo descargar el reporte.";
    throw new Error(msg);
  }
};

export const descargarReporteInversionistas = () =>
  getCsv("/inversionistas", "reporte_inversionistas.csv");

export const descargarReporteComisionistas = () =>
  getCsv("/comisionistas", "reporte_comisionistas.csv");

export const descargarReporteOrdenes = () =>
  getCsv("/ordenes", "reporte_ordenes.csv");
