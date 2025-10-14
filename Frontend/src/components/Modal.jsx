export default function Modal({ open, title, children, onClose, footer }) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div
        className="absolute inset-0 bg-black/40"
        onClick={onClose}
        aria-hidden
      />
      <div className="relative bg-white rounded shadow-lg max-w-2xl w-[92%] max-h-[85vh] overflow-hidden">
        <div className="px-4 py-3 border-b">
          <h3 className="text-lg font-semibold">{title}</h3>
        </div>
        <div className="p-4 overflow-auto" style={{ maxHeight: "60vh" }}>
          {children}
        </div>
        {footer && <div className="px-4 py-3 border-t bg-gray-50">{footer}</div>}
      </div>
    </div>
  );
}
