import cuentasApi from "./axiosCuentas";

export const obtenerSaldo = async () => {
  const res = await cuentasApi.get("/saldo");
  const h = res?.headers || {};
  const notice = h["x-notice"] ?? h["X-Notice"] ?? null;
  return { data: res.data, notice };
};

export const crearRelacionACH = async (payload) => {
  const res = await cuentasApi.post("/ach/crear", payload);
  return res.data;
};

export const obtenerRelacionesACH = async () => {
  const res = await cuentasApi.get("/ach/obtener");
  return res.data;
};

export const crearTransferencia = async (amount) => {
  const res = await cuentasApi.post("/transferencias/crear", { amount });
  return res.data;
};
