import axios from "axios";

const backupApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/respaldo",
  withCredentials: true,
});

backupApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default backupApi;
