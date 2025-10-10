import inversionistasApi from "./axiosInversionistas";

export const registrarInversionista = async (payload) => {
  const { data } = await inversionistasApi.post("/registrar", payload);
  return data;
};

export const getMiPerfil = async () => {
  const { data } = await inversionistasApi.get("/perfil");
  return data;
};

export const actualizarMiPerfil = async (payload) => {
  const { data } = await inversionistasApi.put("/actualizar", payload);
  return data;
};
