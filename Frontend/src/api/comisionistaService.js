import comisionistasApi from "./axiosComisionistas";

// Lista todos los comisionistas (lo que ya tienes en el backend)
export const listarComisionistas = async () => {
  const { data } = await comisionistasApi.get("/listar");
  return data; // array de comisionistas
};

// Obtener detalle por ID (por si lo necesitas)
export const obtenerComisionistaPorId = async (id) => {
  const { data } = await comisionistasApi.get(`/${id}`);
  return data;
};

// Perfil del comisionista autenticado (si lo usas desde rol COMISIONISTA)
export const getMiPerfilComisionista = async () => {
  const { data } = await comisionistasApi.get("/perfil");
  return data;
};
