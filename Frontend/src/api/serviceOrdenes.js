import ordenesApi from "./axiosOrdenes";

const unwrap = (p) =>
  p.then((r) => r.data).catch((e) => {
    const payload = e?.response?.data || { message: e.message || "Error" };
    throw payload;
  });

const toSnakeOrderPayload = (p = {}) => {
  const symbol = p.symbol?.trim();
  const qty = (p.qty ?? p.quantity ?? "").toString().trim();
  const side = p.side?.toLowerCase();    
  const type = p.type?.toLowerCase();     
  const time_in_force = (p.time_in_force ?? p.timeInForce ?? p.timeinforce ?? "")
    .toString()
    .trim()
    .toLowerCase();                   
  const limit_price = p.limit_price ?? p.limitPrice;
  const stop_price = p.stop_price ?? p.stopPrice;

  if (!symbol) throw { message: "symbol es obligatorio" };
  if (!qty) throw { message: "qty es obligatorio" };
  if (!side) throw { message: "side es obligatorio" };
  if (!type) throw { message: "type es obligatorio" };
  if (!time_in_force) throw { message: "time_in_force es obligatorio" };

  return {
    symbol,
    qty,
    side,
    type,
    time_in_force,
    ...(limit_price != null ? { limit_price: String(limit_price) } : {}),
    ...(stop_price != null ? { stop_price: String(stop_price) } : {}),
  };
};

export const crearOrden = async (payload) =>
  unwrap(ordenesApi.post("/ordenes/crear", toSnakeOrderPayload(payload)));

export const getMisOrdenes = async () =>
  unwrap(ordenesApi.get("/ordenes/mis-ordenes"));

export const getOrdenById = async (id) =>
  unwrap(ordenesApi.get(`/ordenes/${encodeURIComponent(id)}`));

export const getMisPosiciones = async () =>
  unwrap(ordenesApi.get("/ordenes/mis-posiciones"));

export const comisionistaMisOrdenes = async (params = {}) =>
  unwrap(ordenesApi.get("/ordenes/comisionista/mis-ordenes", { params }));

export const comisionistaOrdenesPorInversionista = async (inversionistaId) =>
  unwrap(
    ordenesApi.get(
      `/ordenes/comisionista/inversionistas/${encodeURIComponent(inversionistaId)}`
    )
  );

export const comisionistaAprobarOrden = async (id, body = {}) =>
  unwrap(
    ordenesApi.post(
      `/ordenes/comisionista/${encodeURIComponent(id)}/aprobar`,
      body
    )
  );

export const comisionistaRechazarOrden = async (id, body) =>
  unwrap(
    ordenesApi.post(
      `/ordenes/comisionista/${encodeURIComponent(id)}/rechazar`,
      body
    )
  );

export const getResumenComisiones = async (params = {}) => {
  const clean = {};
  if (params.from) clean.from = params.from;
  if (params.to) clean.to = params.to;

  try {
    const { data } = await ordenesApi.get(
      "/ordenes/comisionista/comisiones/resumen",
      { params: clean }
    );
    return data;
  } catch (e) {
    const payload = e?.response?.data || { message: e.message };
    throw payload;
  }
};

export const getMarketStatus = async () =>
  unwrap(ordenesApi.get("/mercado/market-status"));

export const getQuote = async (symbol) =>
  unwrap(ordenesApi.get("/mercado/quote", { params: { symbol } }));

export const searchInstruments = async (query) =>
  unwrap(ordenesApi.get("/mercado/search", { params: { query } }));
