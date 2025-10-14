import comisionistasApi from "./axiosComisionistas";

export const registrarComisionista = async (payload) => {
  const { data } = await comisionistasApi.post("/registrar", payload);
  return data;
};

export const listarComisionistas = async () => {
  const { data } = await comisionistasApi.get("/listar");
  return data; 
};

export const obtenerComisionistaPorId = async (id) => {
  const { data } = await comisionistasApi.get(`/${id}`);
  return data;
};

export const getMiPerfilComisionista = async () => {
  const { data } = await comisionistasApi.get("/perfil");
  return data;
};
