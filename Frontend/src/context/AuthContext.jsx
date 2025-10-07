import { createContext, useContext, useEffect, useRef, useState } from "react";
import {
  login as loginService,
  logout as logoutService,
  refresh as refreshService,
  getMe as getMeService,
} from "../api/authService";

const parseBackendMessage = (err, fallback = "Ocurrió un error") => {
  const m = err?.response?.data?.message || err?.response?.data?.error;
  return m || fallback;
};

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [accessToken, setAccessToken] = useState(
    () => localStorage.getItem("accessToken") || null
  );

  const refreshingRef = useRef(null);

  const setAccess = (token) => {
    if (token) {
      localStorage.setItem("accessToken", token);
      setAccessToken(token);
    } else {
      localStorage.removeItem("accessToken");
      setAccessToken(null);
    }
  };

  const loadMe = async () => {
    try {
      const me = await getMeService();
      setUser(me);
      return me;
    } catch (err) {
      const status = err?.response?.status;
      if (status === 401 || status === 403) {
        throw err;
      }
      throw err;
    }
  };

  const doRefreshOnce = async () => {
    if (!refreshingRef.current) {
      refreshingRef.current = (async () => {
        const res = await refreshService();
        if (res?.accessToken) {
          setAccess(res.accessToken);
          return res.accessToken;
        }
        throw new Error("REFRESH_SIN_TOKEN");
      })()
        .catch((e) => {
          setAccess(null);
          setUser(null);
          throw e;
        })
        .finally(() => {
          refreshingRef.current = null;
        });
    }
    return refreshingRef.current;
  };

  useEffect(() => {
    const init = async () => {
      try {
        if (!accessToken) {
          await doRefreshOnce();
        }
        await loadMe();
      } catch (err) {
        setAccess(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };
    init();
  }, []);

  const handleLogin = async (correo, contrasena) => {
    const res = await loginService(correo, contrasena);
    setAccess(res?.accessToken || null);

    const me = await loadMe().catch((err) => {
      const status = err?.response?.status;
      if (status === 401 || status === 403) {
        return doRefreshOnce().then(loadMe);
      }
      throw err;
    });

    setUser(me);
    +(+localStorage.setItem("hasLoggedIn", "1"));
    return res;
  };

  const handleLogout = async () => {
    try {
      await logoutService();
    } catch (e) {
    } finally {
      setAccess(null);
      setUser(null);
      +(+localStorage.removeItem("hasLoggedIn"));
    }
  };

  const ensureAuthenticated = async () => {
    if (user) return user;

    try {
      await doRefreshOnce();
      const me = await loadMe();
      setUser(me);
      return me;
    } catch {
      await handleLogout();
      throw new Error("NO_AUTENTICADO");
    }
  };

  const value = {
    user,
    accessToken,
    loading,
    isAuthenticated: !!user,
    login: handleLogin,
    logout: handleLogout,
    ensureAuthenticated,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);
