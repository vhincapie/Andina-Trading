import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState, useCallback } from "react";
import { getMiContratoActivo } from "../api/contratoService";

export default function Navbar() {
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuth();

  const role = String(user?.rol || "").toUpperCase();
  const rolesArr = Array.isArray(user?.roles)
    ? user.roles.map((r) => String(r).toUpperCase())
    : [];

  const isInvestor =
    ["INVESTOR", "INVERSIONISTA"].includes(role) ||
    rolesArr.some((r) => ["INVESTOR", "INVERSIONISTA"].includes(r));

  const isComi = role === "COMISIONISTA" || rolesArr.includes("COMISIONISTA");
  const isAdmin =
    ["ADMIN", "ADMINISTRADOR"].includes(role) ||
    rolesArr.some((r) => ["ADMIN", "ADMINISTRADOR"].includes(r));

  const [hasContract, setHasContract] = useState(false);
  const [contractLoading, setContractLoading] = useState(false);

  const loadContract = useCallback(async () => {
    if (!isAuthenticated || !isInvestor) {
      setHasContract(false);
      return;
    }
    setContractLoading(true);
    try {
      const c = await getMiContratoActivo().catch(() => null);
      setHasContract(!!c && c.estado === "ACTIVO");
    } finally {
      setContractLoading(false);
    }
  }, [isAuthenticated, isInvestor]);

  useEffect(() => {
    loadContract();
  }, [loadContract]);

  useEffect(() => {
    const onContractChanged = () => loadContract();
    window.addEventListener("contract:changed", onContractChanged);

    const onVisibility = () => {
      if (document.visibilityState === "visible") loadContract();
    };
    document.addEventListener("visibilitychange", onVisibility);

    return () => {
      window.removeEventListener("contract:changed", onContractChanged);
      document.removeEventListener("visibilitychange", onVisibility);
    };
  }, [loadContract]);

  const onLogout = async () => {
    try {
      await logout();
    } finally {
      navigate("/login", { replace: true });
    }
  };

  const [collapsed, setCollapsed] = useState(false);

  const linkBaseClass =
    "group flex items-center gap-3 px-3 py-2 rounded text-sm transition-colors";
  const activeClass =
    "bg-slate-800 text-emerald-300 ring-1 ring-emerald-400/30";
  const inactiveClass = "text-slate-300 hover:text-white hover:bg-slate-800/60";
  const itemIcon = "shrink-0 h-5 w-5 opacity-80 group-hover:opacity-100";

  const CatalogLinks = () => (
    <>
      <NavLink
        to="/catalogos/paises"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
        title="Países"
      >
        <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
          <path d="M3 5h18v2H3V5zm0 6h18v2H3v-2zm0 6h18v2H3v-2z" />
        </svg>
        {!collapsed && <span>Países</span>}
      </NavLink>
      <NavLink
        to="/catalogos/ciudades"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
        title="Ciudades"
      >
        <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
          <path d="M12 2l7 6v12H5V8l7-6zm0 2.5L7 8v10h10V8l-5-3.5z" />
        </svg>
        {!collapsed && <span>Ciudades</span>}
      </NavLink>
      <NavLink
        to="/catalogos/situaciones"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
        title="Situaciones"
      >
        <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
          <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8v-10h-8v10zm0-18v6h8V3h-8z" />
        </svg>
        {!collapsed && <span>Situaciones</span>}
      </NavLink>
    </>
  );

  const ContractMiniStatus = () =>
    contractLoading ? (
      <div
        className={`mx-3 my-2 rounded bg-slate-800/50 h-6 animate-pulse ${
          collapsed ? "w-8" : "w-36"
        }`}
        title="Verificando contrato…"
      />
    ) : null;

  return (
    <aside
      className={`fixed z-40 left-0 top-0 h-screen bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950/95 text-slate-100
      border-r border-slate-800/60 backdrop-blur supports-[backdrop-filter]:backdrop-blur-xl
      transition-all duration-300 ${collapsed ? "w-16" : "w-64"}`}
    >
      <div className="flex items-center justify-between h-14 px-3">
        <div className="flex items-center gap-2">
          <div className="h-8 w-8 rounded-lg bg-slate-800 grid place-content-center ring-1 ring-slate-700">
            <svg
              viewBox="0 0 24 24"
              className="h-4 w-4 text-emerald-300"
              fill="currentColor"
            >
              <path d="M3 12l9-9 9 9-1.5 1.5L12 5.5 4.5 13.5 3 12z" />
            </svg>
          </div>
          {!collapsed && (
            <span className="font-semibold tracking-tight">
              Foresta Trading
            </span>
          )}
        </div>

        <button
          onClick={() => setCollapsed((v) => !v)}
          className="p-2 rounded-lg hover:bg-slate-800 ring-1 ring-slate-800"
          title={collapsed ? "Expandir" : "Contraer"}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="currentColor">
            {collapsed ? (
              <path d="M9 6l6 6-6 6V6z" />
            ) : (
              <path d="M15 18l-6-6 6-6v12z" />
            )}
          </svg>
        </button>
      </div>

      <div className="px-2 pb-24 overflow-y-auto h-[calc(100vh-56px)]">
        <NavLink
          to="/"
          end
          className={({ isActive }) =>
            `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
          }
          title="Inicio"
        >
          <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
            <path d="M12 3l10 9h-3v9h-6v-6H11v6H5v-9H2l10-9z" />
          </svg>
          {!collapsed && <span>Inicio</span>}
        </NavLink>

        {isAuthenticated && isInvestor && (
          <>
            <div
              className={`mt-4 mb-1 ${
                collapsed ? "px-1" : "px-3"
              } text-xs uppercase tracking-wider text-slate-400`}
            >
              {!collapsed && "Inversionista"}
            </div>

            <NavLink
              to="/perfil"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Mi perfil"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm-9 9a9 9 0 0 1 18 0H3z" />
              </svg>
              {!collapsed && <span>Mi perfil</span>}
            </NavLink>

            <NavLink
              to="/contratos"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Contrato"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M6 2h9l5 5v15H6zM8 4h6v4h4v12H8z" />
              </svg>
              {!collapsed && <span>Contrato</span>}
            </NavLink>

            <NavLink
              to="/cuenta-bancaria"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Cuenta bancaria"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M3 10l9-6 9 6v2H3v-2zm0 4h18v6H3v-6z" />
              </svg>
              {!collapsed && <span>Cuenta bancaria</span>}
            </NavLink>

            <ContractMiniStatus />

            {hasContract && (
              <>
                <NavLink
                  to="/ordenes/nueva"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                  title="Comprar"
                >
                  <svg
                    viewBox="0 0 24 24"
                    className={itemIcon}
                    fill="currentColor"
                  >
                    <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM7 6h14l-1.5 6h-11L7 6z" />
                  </svg>
                  {!collapsed && <span>Comprar</span>}
                </NavLink>

                <NavLink
                  to="/ordenes/vender"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                  title="Vender"
                >
                  <svg
                    viewBox="0 0 24 24"
                    className={itemIcon}
                    fill="currentColor"
                  >
                    <path d="M5 4h14v2H5zm0 14h14v2H5zm0-7h14v2H5z" />
                  </svg>
                  {!collapsed && <span>Vender</span>}
                </NavLink>

                <NavLink
                  to="/ordenes/mias"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                  title="Mis órdenes"
                >
                  <svg
                    viewBox="0 0 24 24"
                    className={itemIcon}
                    fill="currentColor"
                  >
                    <path d="M4 4h16v4H4zm0 6h10v4H4zm0 6h16v4H4z" />
                  </svg>
                  {!collapsed && <span>Mis órdenes</span>}
                </NavLink>
              </>
            )}
          </>
        )}

        {isAuthenticated && isComi && (
          <>
            <div
              className={`mt-4 mb-1 ${
                collapsed ? "px-1" : "px-3"
              } text-xs uppercase tracking-wider text-slate-400`}
            >
              {!collapsed && "Comisionista"}
            </div>
            <NavLink
              to="/comisionista/perfil"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Mi perfil"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm-9 9a9 9 0 0 1 18 0H3z" />
              </svg>
              {!collapsed && <span>Mi perfil</span>}
            </NavLink>
            <NavLink
              to="/comisionista/contratos"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Contratos"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M6 2h9l5 5v15H6zM8 4h6v4h4v12H8z" />
              </svg>
              {!collapsed && <span>Contratos</span>}
            </NavLink>
            <NavLink
              to="/comisionista/ordenes"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Órdenes"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M4 4h16v4H4zm0 6h10v4H4zm0 6h16v4H4z" />
              </svg>
              {!collapsed && <span>Órdenes</span>}
            </NavLink>
            <NavLink
              to="/comisionista/comisiones"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Comisiones"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M3 17h2v-7H3v7zm4 0h2V7H7v10zm4 0h2v-4h-2v4zm4 0h2V4h-2v13zm4 0h2v-9h-2v9z" />
              </svg>
              {!collapsed && <span>Comisiones</span>}
            </NavLink>
          </>
        )}

        {isAuthenticated && isAdmin && (
          <>
            <div
              className={`mt-4 mb-1 ${
                collapsed ? "px-1" : "px-3"
              } text-xs uppercase tracking-wider text-slate-400`}
            >
              {!collapsed && "Administrador"}
            </div>
            <NavLink
              to="/admin/comisionistas"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Comisionistas"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zM8 11c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C15 14.17 10.33 13 8 13zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h7v-2.5c0-2.33-4.67-3.5-8-3.5z" />
              </svg>
              {!collapsed && <span>Comisionistas</span>}
            </NavLink>
            <NavLink
              to="/admin/consolidacion"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Consolidación"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z" />
              </svg>
              {!collapsed && <span>Consolidación</span>}
            </NavLink>
            <NavLink
              to="/admin/reportes"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Reportes"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M3 3h18v2H3V3zm0 4h10v2H3V7zm0 4h14v2H3v-2zm0 4h18v2H3v-2z" />
              </svg>
              {!collapsed && <span>Reportes</span>}
            </NavLink>
            <NavLink
              to="/admin/auditoria"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Auditoría"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M3 5h18v2H3zM3 9h12v2H3zM3 13h18v2H3zM3 17h12v2H3z" />
              </svg>
              {!collapsed && <span>Auditoría</span>}
            </NavLink>
            <NavLink
              to="/admin/respaldo"
              className={({ isActive }) =>
                `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
              }
              title="Respaldo"
            >
              <svg viewBox="0 0 24 24" className={itemIcon} fill="currentColor">
                <path d="M12 2a10 10 0 1 0 10 10H20A8 8 0 1 1 12 4v4l5-5-5-5v6z" />
              </svg>
              {!collapsed && <span>Respaldo</span>}
            </NavLink>
            <CatalogLinks />
          </>
        )}
      </div>

      <div className="absolute bottom-0 left-0 right-0 border-t border-slate-800/60 p-2">
        {isAuthenticated ? (
          <div
            className={`flex items-center ${
              collapsed ? "justify-center" : "justify-between"
            } gap-2`}
          >
            {!collapsed && (
              <div className="text-xs leading-tight text-slate-300">
                <div className="truncate">{user?.correo}</div>
                <div className="text-emerald-300/90 font-semibold">
                  {role || "USER"}
                </div>
              </div>
            )}
            <button
              onClick={onLogout}
              className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-100 text-sm ring-1 ring-slate-700"
              title="Salir"
            >
              {collapsed ? "⎋" : "Salir"}
            </button>
          </div>
        ) : (
          <div
            className={`flex ${
              collapsed
                ? "flex-col items-center gap-2"
                : "justify-between gap-2"
            }`}
          >
            <NavLink
              to="/login"
              className={({ isActive }) =>
                `px-3 py-1.5 rounded-lg text-sm ring-1 ring-slate-700 ${
                  isActive
                    ? "bg-slate-800 text-emerald-300"
                    : "text-slate-200 hover:bg-slate-800"
                }`
              }
              title="Iniciar sesión"
            >
              {collapsed ? "Iniciar" : "Iniciar sesión"}
            </NavLink>
            <NavLink
              to="/registro-inversionista"
              className={({ isActive }) =>
                `px-3 py-1.5 rounded-lg text-sm ring-1 ring-slate-700 ${
                  isActive
                    ? "bg-slate-800 text-emerald-300"
                    : "text-slate-200 hover:bg-slate-800"
                }`
              }
              title="Registrarme"
            >
              {collapsed ? "Registro" : "Registrarme"}
            </NavLink>
          </div>
        )}
      </div>
    </aside>
  );
}
