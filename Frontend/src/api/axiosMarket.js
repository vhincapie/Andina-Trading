import axios from "axios";

const marketApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/market",
  withCredentials: true,
});

marketApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default marketApi;
