import axios from "axios";

const base = import.meta.env.VITE_CUENTAS_BASE_URL
  ? `${import.meta.env.VITE_CUENTAS_BASE_URL}/api/cuentas`
  : "/api/cuentas";

const cuentasApi = axios.create({
  baseURL: base,
  withCredentials: false,
});

cuentasApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default cuentasApi;
