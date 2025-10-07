export default function ErrorAlert({ message, onClose }) {
  if (!message) return null;

  return (
    <div
      className="rounded border border-red-300 bg-red-50 text-red-800 px-3 py-2 relative"
      style={{ marginBottom: "8px" }}
    >
      <span>{message}</span>
      {onClose && (
        <button
          onClick={onClose}
          className="absolute right-2 top-1 text-red-600 hover:text-red-800"
        >
          ×
        </button>
      )}
    </div>
  );
}
