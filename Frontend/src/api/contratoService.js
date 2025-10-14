import contratosApi from "./axiosContratos";

// Registrar contrato (INVERSIONISTA)
export const registrarContrato = async (payload) => {
  // payload: { comisionistaId, moneda, observaciones, aceptaTerminos }
  const { data } = await contratosApi.post("/registrar", payload);
  return data; // ContratoDTO enriquecido
};

// Obtener mi contrato activo (INVERSIONISTA)
export const getMiContratoActivo = async () => {
  const { data } = await contratosApi.get("/mi-contrato");
  return data;
};

// Cancelar mi contrato activo (INVERSIONISTA)
export const cancelarMiContratoActivo = async () => {
  const { data } = await contratosApi.put("/cancelar");
  return data;
};

// Consultar contrato ACTIVO de un inversionista (COMISIONISTA)
export const getContratoActivoDeInversionista = async (inversionistaId) => {
  const { data } = await contratosApi.get(`/${inversionistaId}`);
  return data;
};
