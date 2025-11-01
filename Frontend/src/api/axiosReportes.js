import axios from "axios";

const reportesApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/reportes",
  withCredentials: true,
});

reportesApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default reportesApi;
