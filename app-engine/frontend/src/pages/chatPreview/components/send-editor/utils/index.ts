export const messagePaste = (e) => {
  e.preventDefault();
  let items = e.clipboardData?.items || [];
  for (let i = 0; i < items?.length; i++) {
    const item = items[i];
    if (item.kind === "string" && item.type === "text/plain") {
      item.getAsString(function (str) {
        document.execCommand("insertText", true, str);
      });
    }
  }
}