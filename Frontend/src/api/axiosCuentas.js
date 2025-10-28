import axios from "axios";

const cuentasApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/cuentas",
  withCredentials: true, 
});

cuentasApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default cuentasApi;
