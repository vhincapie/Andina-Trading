import axios from "axios";

const catalogosApi = axios.create({
  baseURL: "/api/catalogos",
  withCredentials: true,
});

catalogosApi.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default catalogosApi;
