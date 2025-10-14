import contratosApi from "./axiosContratos";

export const registrarContrato = async (payload) => {
  const { data } = await contratosApi.post("/registrar", payload);
  return data; 
};

export const getMiContratoActivo = async () => {
  const { data } = await contratosApi.get("/mi-contrato");
  return data;
};

export const cancelarMiContratoActivo = async () => {
  const { data } = await contratosApi.put("/cancelar");
  return data;
};

export const listarMisContratosComisionista = async () => {
  const { data } = await contratosApi.get("/mis-contratos");
  return data;
};
