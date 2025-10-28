import axios from "axios";

const authApi = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || "") + "/api/auth",
  withCredentials: true,
});

authApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default authApi;
