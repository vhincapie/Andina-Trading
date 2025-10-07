import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Auth (8081)
      "/api/auth": {
        target: "http://localhost:8081",
        changeOrigin: true,
        secure: false,
      },
      // Catálogos (8082)
      "/api": {
        target: "http://localhost:8082",
        changeOrigin: true,
        secure: false,
        bypass(req) {
          if (req.url.startsWith("/api/auth")) return "/api/auth";
        },
      },
    },
  },
});
