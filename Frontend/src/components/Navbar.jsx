import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

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

  const onLogout = async () => {
    try {
      await logout();
    } finally {
      navigate("/login", { replace: true });
    }
  };

  const linkBaseClass = "px-3 py-2 rounded text-sm transition-colors";
  const activeClass = "bg-gray-900 text-white";
  const inactiveClass = "text-gray-700 hover:bg-gray-100";

  const CatalogLinks = () => (
    <>
      <NavLink
        to="/catalogos/paises"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
      >
        Países
      </NavLink>
      <NavLink
        to="/catalogos/ciudades"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
      >
        Ciudades
      </NavLink>
      <NavLink
        to="/catalogos/situaciones"
        className={({ isActive }) =>
          `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
        }
      >
        Situaciones
      </NavLink>
    </>
  );

  return (
    <nav className="w-full border-b bg-white/90 backdrop-blur">
      <div className="max-w-5xl mx-auto px-4">
        <div className="flex items-center justify-between h-14">
          <div className="flex items-center gap-3">
            <span className="font-semibold text-gray-900">Foresta Trading</span>

            <div className="hidden md:flex items-center gap-1 ml-2">
              <NavLink
                to="/"
                end
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Inicio
              </NavLink>

              {isAuthenticated && isInvestor && (
                <>
                  <NavLink
                    to="/perfil"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Mi perfil
                  </NavLink>
                  <NavLink
                    to="/contratos"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Contrato
                  </NavLink>
                  <NavLink
                    to="/cuenta-bancaria"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Cuenta bancaria
                  </NavLink>
                  <NavLink
                    to="/recargar"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Recargar
                  </NavLink>
                </>
              )}

              {isAuthenticated && isComi && (
                <>
                  <NavLink
                    to="/comisionista/perfil"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Mi perfil
                  </NavLink>
                  <NavLink
                    to="/comisionista/contratos"
                    className={({ isActive }) =>
                      `${linkBaseClass} ${
                        isActive ? activeClass : inactiveClass
                      }`
                    }
                  >
                    Contratos
                  </NavLink>
                </>
              )}

              {isAuthenticated && isAdmin && (
                <NavLink
                  to="/admin/comisionistas"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                >
                  Comisionistas
                </NavLink>
              )}
              {isAuthenticated && isAdmin && <CatalogLinks />}
            </div>
          </div>

          <div className="flex items-center gap-2">
            {isAuthenticated ? (
              <>
                <span className="hidden sm:inline text-sm text-gray-600">
                  {user?.correo} · <strong>{role || "USER"}</strong>
                </span>
                <button
                  onClick={onLogout}
                  className="px-3 py-1.5 rounded bg-gray-200 hover:bg-gray-300 text-gray-800 text-sm"
                >
                  Salir
                </button>
              </>
            ) : (
              <>
                <NavLink
                  to="/login"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                >
                  Iniciar sesión
                </NavLink>
                <NavLink
                  to="/registro-inversionista"
                  className={({ isActive }) =>
                    `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                  }
                >
                  Registrarme
                </NavLink>
              </>
            )}
          </div>
        </div>

        <div className="md:hidden flex gap-1 pb-3">
          <NavLink
            to="/"
            end
            className={({ isActive }) =>
              `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
            }
          >
            Inicio
          </NavLink>

          {isAuthenticated && isInvestor && (
            <>
              <NavLink
                to="/perfil"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Mi perfil
              </NavLink>
              <NavLink
                to="/contratos"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Contrato
              </NavLink>
              <NavLink
                to="/cuenta-bancaria"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Cuenta bancaria
              </NavLink>
              <NavLink
                to="/recargar"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Recargar
              </NavLink>
            </>
          )}

          {isAuthenticated && isComi && (
            <>
              <NavLink
                to="/comisionista/perfil"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Mi perfil
              </NavLink>
              <NavLink
                to="/comisionista/contratos"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Contratos
              </NavLink>
            </>
          )}

          {isAuthenticated && isAdmin && (
            <>
              <NavLink
                to="/admin/comisionistas"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Comisionistas
              </NavLink>
              <CatalogLinks />
            </>
          )}

          {!isAuthenticated && (
            <>
              <NavLink
                to="/login"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Iniciar sesión
              </NavLink>
              <NavLink
                to="/registro-inversionista"
                className={({ isActive }) =>
                  `${linkBaseClass} ${isActive ? activeClass : inactiveClass}`
                }
              >
                Registrarme
              </NavLink>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
