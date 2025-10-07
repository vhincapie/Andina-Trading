import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth(); 

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
            </div>
          </div>

          <div className="flex items-center gap-3">
            {user && (
              <span className="hidden sm:inline text-sm text-gray-600">
                {user.correo} · <strong>{user.rol}</strong>
              </span>
            )}

            <button
              onClick={onLogout}
              className="px-3 py-1.5 rounded bg-gray-200 hover:bg-gray-300 text-gray-800 text-sm"
              disabled={!user}
              title={!user ? "No has iniciado sesión" : "Cerrar sesión"}
            >
              Salir
            </button>
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
        </div>
      </div>
    </nav>
  );
}
