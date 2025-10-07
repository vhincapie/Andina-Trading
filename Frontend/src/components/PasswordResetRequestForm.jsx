export default function PasswordResetRequestForm({
  correo,
  setCorreo,
  loading,
  error,
  ok,
  onSubmit,
}) {
  return (
    <form onSubmit={onSubmit} className="max-w-sm mx-auto mt-16 space-y-4">
      <h1 className="text-2xl font-semibold text-center">
        Recuperar contraseña
      </h1>

      {error && <p className="text-red-600 text-sm">{error}</p>}
      {ok && (
        <p className="text-green-600 text-sm">
          Si el correo existe, te enviaremos un enlace para restablecer tu
          contraseña.
        </p>
      )}

      <input
        type="email"
        placeholder="Correo"
        className="w-full border p-2 rounded"
        value={correo}
        onChange={(e) => setCorreo(e.target.value)}
        required
      />

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-60"
      >
        {loading ? "Enviando..." : "Enviar enlace"}
      </button>
    </form>
  );
}
