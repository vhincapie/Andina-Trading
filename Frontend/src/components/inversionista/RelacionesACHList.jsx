export default function RelacionesACHList({ items = [] }) {
  if (!Array.isArray(items) || items.length === 0) {
    return <p className="text-gray-600">Aún no tienes cuentas asociadas.</p>;
  }

  const mapTipo = (t) => {
    const v = String(t || "").toUpperCase();
    if (v === "CHECKING") return "Corriente";
    if (v === "SAVINGS") return "Ahorros";
    return v || "—";
  };

  return (
    <>
      <div className="hidden md:block overflow-x-auto">
        <table className="min-w-full border rounded bg-white">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-2 px-3 text-left text-sm">Titular</th>
              <th className="py-2 px-3 text-left text-sm">Banco</th>
              <th className="py-2 px-3 text-left text-sm">Número</th>
              <th className="py-2 px-3 text-left text-sm">Tipo</th>
            </tr>
          </thead>
          <tbody>
            {items.map((c) => (
              <tr
                key={c.id || `${c.nickname}-${c.bankAccountNumber}`}
                className="border-t"
              >
                <td className="py-2 px-3 text-sm">
                  {c.accountOwnerName || c.account_owner_name}
                </td>
                <td className="py-2 px-3 text-sm">{c.nickname}</td>
                <td className="py-2 px-3 text-sm">
                  ****
                  {String(
                    c.bankAccountNumber || c.bank_account_number || ""
                  ).slice(-4)}
                </td>
                <td className="py-2 px-3 text-sm">
                  {mapTipo(c.bankAccountType || c.bank_account_type)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="md:hidden space-y-3">
        {items.map((c) => (
          <div
            key={c.id || `${c.nickname}-${c.bankAccountNumber}`}
            className="border rounded p-3 bg-white"
          >
            <p className="text-sm">
              <span className="font-medium">Titular:</span>{" "}
              {c.accountOwnerName || c.account_owner_name}
            </p>
            <p className="text-sm">
              <span className="font-medium">Banco:</span> {c.nickname}
            </p>
            <p className="text-sm">
              <span className="font-medium">Número:</span> ****
              {String(c.bankAccountNumber || c.bank_account_number || "").slice(
                -4
              )}
            </p>
            <p className="text-sm">
              <span className="font-medium">Tipo:</span>{" "}
              {mapTipo(c.bankAccountType || c.bank_account_type)}
            </p>
            <p className="text-sm">
              <span className="font-medium">Estado:</span> {c.status || "—"}
            </p>
          </div>
        ))}
      </div>
    </>
  );
}
