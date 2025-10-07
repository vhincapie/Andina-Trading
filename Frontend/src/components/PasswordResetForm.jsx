export default function PasswordResetForm({
  nuevaContrasena,
  setNuevaContrasena,
  confirmar,
  setConfirmar,
  loading,
  error,
  ok,
  onSubmit,
}) {
  return (
    <form onSubmit={onSubmit} className="max-w-sm mx-auto mt-16 space-y-4">
      <h1 className="text-2xl font-semibold text-center">
        Restablecer contraseña
      </h1>

      {error && <p className="text-red-600 text-sm">{error}</p>}
      {ok && (
        <p className="text-green-600 text-sm">
          Contraseña actualizada. Ya puedes iniciar sesión.
        </p>
      )}

      <input
        type="password"
        placeholder="Nueva contraseña"
        className="w-full border p-2 rounded"
        value={nuevaContrasena}
        onChange={(e) => setNuevaContrasena(e.target.value)}
        required
        minLength={6}
      />
      <input
        type="password"
        placeholder="Confirmar contraseña"
        className="w-full border p-2 rounded"
        value={confirmar}
        onChange={(e) => setConfirmar(e.target.value)}
        required
        minLength={6}
      />

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-60"
      >
        {loading ? "Guardando..." : "Restablecer"}
      </button>
    </form>
  );
}
