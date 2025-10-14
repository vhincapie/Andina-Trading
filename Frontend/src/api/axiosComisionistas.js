import axios from "axios";

const comisionistasApi = axios.create({
  baseURL:
    (import.meta.env.VITE_COMISIONISTAS_BASE_URL || "") + "/api/comisionistas",
  withCredentials: true,
});

comisionistasApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default comisionistasApi;
