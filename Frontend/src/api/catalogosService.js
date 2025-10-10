import catalogosApi from "./axiosCatalogos.js";

export const listarPaises = async () => {
  const { data } = await catalogosApi.get("/catalogos/paises/listar");
  return data;
};
export const crearPais = async (payload) => {
  const { data } = await catalogosApi.post("/catalogos/paises/crear", payload);
  return data;
};

export const listarCiudades = async () => {
  const { data } = await catalogosApi.get("/catalogos/ciudades/listar");
  return data;
};
export const crearCiudad = async (payload) => {
  const { data } = await catalogosApi.post(
    "/catalogos/ciudades/crear",
    payload
  );
  return data;
};

export const listarSituaciones = async () => {
  const { data } = await catalogosApi.get(
    "/catalogos/situaciones-economicas/listar"
  );
  return data;
};
export const crearSituacion = async (payload) => {
  const { data } = await catalogosApi.post(
    "/catalogos/situaciones-economicas/crear",
    payload
  );
  return data;
};
