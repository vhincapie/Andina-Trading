import authApi from "./axiosAuth";

export const login = async (correo, contrasena) => {
  const res = await authApi.post("/login", { correo, contrasena });
  return res.data;
};

export const refresh = async () => {
  const res = await authApi.post("/refresh");
  return res.data;
};

export const logout = async () => {
  await authApi.post("/logout");
};

export const solicitarReset = async (correo) => {
  await authApi.post("/recuperar-password", { correo });
};

export const restablecerPassword = async (token, nuevaContrasena) => {
  await authApi.post("/restablecer-password", { token, nuevaContrasena });
};

export const getMe = async () => {
  const res = await authApi.get("/me");
  return res.data;
};
