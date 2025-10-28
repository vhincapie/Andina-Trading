import axios from "axios";

const catalogosApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/catalogos",
  withCredentials: true,
});

catalogosApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default catalogosApi;
