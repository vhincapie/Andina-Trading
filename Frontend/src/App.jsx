// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RecoverPasswordPage from "./pages/RecoverPasswordPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import ProtectedRoute from "./routes/ProtectedRoute";
import Navbar from "./components/Navbar";

import CatalogosLayout from "./pages/catalogos/CatalogosLayout";
import PaisesPage from "./pages/catalogos/PaisesPage";
import CiudadesPage from "./pages/catalogos/CiudadesPage";
import SituacionesPage from "./pages/catalogos/SituacionesPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* App protegida */}
        <Route
          element={
            <ProtectedRoute>
              <>
                <Navbar />
                <div className="max-w-5xl mx-auto px-4 py-6">
                  <CatalogosLayout />
                </div>
              </>
            </ProtectedRoute>
          }
        >
          <Route index element={<HomePage />} />
          <Route path="/catalogos/paises" element={<PaisesPage />} />
          <Route path="/catalogos/ciudades" element={<CiudadesPage />} />
          <Route path="/catalogos/situaciones" element={<SituacionesPage />} />
        </Route>

        {/* Públicas */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/recuperar-password" element={<RecoverPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
