export default function SuccessAlert({ message, onClose }) {
  if (!message) return null;

  return (
    <div
      className="relative rounded-2xl border border-emerald-800/50 bg-emerald-900/40 text-emerald-300 px-4 py-3 shadow-lg ring-1 ring-white/10 backdrop-blur"
      style={{ marginBottom: "10px" }}
    >
      <span className="block text-sm font-medium">{message}</span>
      {onClose && (
        <button
          onClick={onClose}
          className="absolute right-3 top-2 text-emerald-400 hover:text-emerald-200 transition-colors text-lg leading-none"
        >
          ×
        </button>
      )}
    </div>
  );
}
