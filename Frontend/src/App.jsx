import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import ProtectedLayout from "./layouts/ProtectedLayout";
import AdminRoute from "./routes/AdminRoute";
import InvestorRoute from "./routes/InvestorRoute";
import ComisionistaRoute from "./routes/ComisionistaRoute";

import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RecoverPasswordPage from "./pages/RecoverPasswordPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";

import RegistroInversionistaPage from "./pages/RegistroInversionistaPage";
import PerfilInversionistaPage from "./pages/inversionista/PerfilInversionistaPage";

import CatalogosLayout from "./pages/catalogos/CatalogosLayout";
import PaisesPage from "./pages/catalogos/PaisesPage";
import CiudadesPage from "./pages/catalogos/CiudadesPage";
import SituacionesPage from "./pages/catalogos/SituacionesPage";

import ComisionistasPage from "./pages/admin/ComisionistasPage";
import PerfilComisionistaPage from "./pages/comisionista/PerfilComisionistaPage";
import ContratosInversionistaPage from "./pages/contratos/ContratosInversionistaPage";
import ContratosComisionistaPage from "./pages/comisionista/ContratosComisionistaPage";

import CuentaBancariaPage from "./pages/inversionista/CuentaBancariaPage";
import RecargaPage from "./pages/inversionista/RecargaPage";

import BuscarYCrearOrden from "./pages/inversionista/BuscarYCrearOrden";
import VenderOrdenPage from "./pages/inversionista/VenderOrdenPage";
import MisOrdenesPage from "./pages/inversionista/MisOrdenesPage";

import MisOrdenesComisionistaPage from "./pages/comisionista/MisOrdenesComisionistaPage";
import ResumenComisionesPage from "./pages/comisionista/ResumenComisionesPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Públicas */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registro-inversionista" element={<RegistroInversionistaPage />} />
        <Route path="/recuperar-password" element={<RecoverPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />

        {/* Privadas con layout */}
        <Route element={<ProtectedLayout />}>
          <Route index element={<HomePage />} />

          {/* Inversionista */}
          <Route
            path="/perfil"
            element={
              <InvestorRoute>
                <PerfilInversionistaPage />
              </InvestorRoute>
            }
          />
          <Route
            path="/contratos"
            element={
              <InvestorRoute>
                <ContratosInversionistaPage />
              </InvestorRoute>
            }
          />
          <Route
            path="/cuenta-bancaria"
            element={
              <InvestorRoute>
                <CuentaBancariaPage />
              </InvestorRoute>
            }
          />
          <Route
            path="/recargar"
            element={
              <InvestorRoute>
                <RecargaPage />
              </InvestorRoute>
            }
          />

          {/* Órdenes (Inversionista) */}
          <Route
            path="/ordenes/crear"
            element={
              <InvestorRoute>
                <BuscarYCrearOrden />
              </InvestorRoute>
            }
          />

          <Route
            path="/ordenes/vender"
            element={
              <InvestorRoute>
                <VenderOrdenPage />
              </InvestorRoute>
            }
          />
          
          <Route
            path="/ordenes/mis-ordenes"
            element={
              <InvestorRoute>
                <MisOrdenesPage />
              </InvestorRoute>
            }
          />

          {/* Comisionista */}
          <Route
            path="/comisionista/perfil"
            element={
              <ComisionistaRoute>
                <PerfilComisionistaPage />
              </ComisionistaRoute>
            }
          />
          <Route
            path="/comisionista/contratos"
            element={
              <ComisionistaRoute>
                <ContratosComisionistaPage />
              </ComisionistaRoute>
            }
          />
          <Route
            path="/comisionista/ordenes"
            element={
              <ComisionistaRoute>
                <MisOrdenesComisionistaPage />
              </ComisionistaRoute>
            }
          />
          <Route
            path="/comisionista/comisiones"
            element={
              <ComisionistaRoute>
                <ResumenComisionesPage />
              </ComisionistaRoute>
            }
          />

          {/* Admin */}
          <Route
            path="/admin/comisionistas"
            element={
              <AdminRoute allowedRoles={["ADMIN", "ADMINISTRADOR"]}>
                <ComisionistasPage />
              </AdminRoute>
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

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
