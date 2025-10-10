import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import ProtectedLayout from "./layouts/ProtectedLayout";
import AdminRoute from "./routes/AdminRoute";
import InvestorRoute from "./routes/InvestorRoute";

import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RecoverPasswordPage from "./pages/RecoverPasswordPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";

import RegistroInversionistaPage from "./pages/RegistroInversionistaPage";
import PerfilInversionistaPage from "./pages/PerfilInversionistaPage";

import CatalogosLayout from "./pages/catalogos/CatalogosLayout";
import PaisesPage from "./pages/catalogos/PaisesPage";
import CiudadesPage from "./pages/catalogos/CiudadesPage";
import SituacionesPage from "./pages/catalogos/SituacionesPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/registro-inversionista"
          element={<RegistroInversionistaPage />}
        />
        <Route path="/recuperar-password" element={<RecoverPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />

        <Route element={<ProtectedLayout />}>
          <Route index element={<HomePage />} />

          <Route
            path="/perfil"
            element={
              <InvestorRoute>
                <PerfilInversionistaPage />
              </InvestorRoute>
            }
          />

          <Route
            path="/catalogos"
            element={
              <AdminRoute allowedRoles={["ADMIN", "ADMINISTRADOR"]}>
                <CatalogosLayout />
              </AdminRoute>
            }
          >
            <Route path="paises" element={<PaisesPage />} />
            <Route path="ciudades" element={<CiudadesPage />} />
            <Route path="situaciones" element={<SituacionesPage />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
