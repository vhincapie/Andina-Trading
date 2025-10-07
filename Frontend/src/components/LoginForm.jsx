export default function LoginForm({
  correo,
  setCorreo,
  contrasena,
  setContrasena,
  onSubmit,
  error,
}) {
  return (
    <form onSubmit={onSubmit} className="max-w-sm mx-auto mt-20 space-y-3">
      {error && <p className="text-red-600 text-sm">{error}</p>}

      <input
        type="email"
        placeholder="Correo"
        className="w-full border p-2 rounded"
        value={correo}
        onChange={(e) => setCorreo(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Contraseña"
        className="w-full border p-2 rounded"
        value={contrasena}
        onChange={(e) => setContrasena(e.target.value)}
        required
      />
      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        Iniciar sesión
      </button>
    </form>
  );
}
