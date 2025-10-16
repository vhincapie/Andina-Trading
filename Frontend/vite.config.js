import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api/auth": {
        target: "http://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      "/api/catalogos": {
        target: "http://localhost:8082",
        changeOrigin: true,
        secure: false,
      },
      "/api/inversionistas": {
        target: "http://localhost:8083",
        changeOrigin: true,
        secure: false,
      },
      "/api/comisionistas": {
        target: "http://localhost:8084",
        changeOrigin: true,
        secure: false,
      },
      "/api/contratos": {
        target: "http://localhost:8085",
        changeOrigin: true,
        secure: false,
      },
      "/api/cuentas": {
        target: "http://localhost:8086",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
