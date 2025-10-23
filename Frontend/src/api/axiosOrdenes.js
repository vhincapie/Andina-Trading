import axios from "axios";

const ordenesApi = axios.create({
  baseURL: (import.meta.env.VITE_ORDENES_BASE_URL || "") + "/api",
  withCredentials: true,
});

ordenesApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default ordenesApi;
