import catalogosApi from "./axiosCatalogos.js";

export const listarPaises = async () => {
  const { data } = await catalogosApi.get("/paises/listar");
  return data;
};
export const crearPais = async (payload) => {
  const { data } = await catalogosApi.post("/paises/crear", payload);
  return data;
};

export const listarCiudades = async () => {
  const { data } = await catalogosApi.get("/ciudades/listar");
  return data;
};
export const crearCiudad = async (payload) => {
  const { data } = await catalogosApi.post("/ciudades/crear", payload);
  return data;
};

export const listarSituaciones = async () => {
  const { data } = await catalogosApi.get("/situaciones-economicas/listar");
  return data;
};
export const crearSituacion = async (payload) => {
  const { data } = await catalogosApi.post(
    "/situaciones-economicas/crear",
    payload
  );
  return data;
};
