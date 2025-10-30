import axios from "axios";

const auditoriaApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/auditoria/logs",
  withCredentials: true,
});

auditoriaApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default auditoriaApi;
