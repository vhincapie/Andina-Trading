export function getApiErrorMessage(err, fallback = "Ocurrió un error") {
  const data = err?.response?.data;

  if (typeof data === "string" && data.trim()) return data;
  if (data?.message) return data.message;
  if (data?.error) return data.error;

  if (Array.isArray(data?.errors) && data.errors.length > 0) {
    const first = data.errors[0];
    if (typeof first === "string") return first;
    if (first?.defaultMessage) return first.defaultMessage;
    if (first?.message) return first.message;
  }
  return fallback;
}
