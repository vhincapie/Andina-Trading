import axios from "axios";

const inversionistasApi = axios.create({
  baseURL:
    (import.meta.env.VITE_INVERSIONISTAS_BASE_URL || "") +
    "/api/inversionistas",
  withCredentials: true,
});

inversionistasApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default inversionistasApi;
