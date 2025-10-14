import axios from "axios";

const contratosApi = axios.create({
  baseURL: (import.meta.env.VITE_CONTRATOS_BASE_URL || "") + "/api/contratos",
  withCredentials: true,
});

contratosApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default contratosApi;
