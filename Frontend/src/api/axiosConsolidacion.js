import axios from "axios";

const consolidacionApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/consolidacion",
  withCredentials: true,
});

consolidacionApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default consolidacionApi;
