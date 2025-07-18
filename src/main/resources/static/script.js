const API_BASE_URL = "http://localhost:8080";

// --- DOM Elements ---
const messageDisplay = document.getElementById("message-display");
const authSection = document.getElementById("auth-section");
const appSection = document.getElementById("app-section");
const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");
const loginTab = document.getElementById("login-tab");
const registerTab = document.getElementById("register-tab");
const loginEmailInput = document.getElementById("loginEmail");
const loginPasswordInput = document.getElementById("loginPassword");
const loginButton = document.getElementById("loginButton");
const registerEmailInput = document.getElementById("registerEmail");
const registerPasswordInput = document.getElementById("registerPassword");
const registerButton = document.getElementById("registerButton");
const logoutButton = document.getElementById("logoutButton");
const welcomeMessage = document.getElementById("welcome-message");

const goToSearchButton = document.getElementById("goToSearchButton");

const productsTable = document.getElementById("productsTable");
const productsTableBody = document.getElementById("productsTableBody");
const noProductsMessage = document.getElementById("no-products-message");

// Modal elements
const confirmationModal = document.getElementById("confirmationModal");
const confirmDeleteButton = document.getElementById("confirmDeleteButton");
const cancelDeleteButton = document.getElementById("cancelDeleteButton");
let currentRequestIdToDelete = null; // To store the ID of the request being deleted

// Forgot Password elements
const forgotPasswordLink = document.getElementById("forgotPasswordLink");
const forgotPasswordModal = document.getElementById("forgotPasswordModal");
const forgotPasswordForm = document.getElementById("forgotPasswordForm");
const forgotPasswordEmailInput = document.getElementById("forgotPasswordEmail");
const sendResetLinkButton = document.getElementById("sendResetLinkButton");
const cancelResetPasswordButton = document.getElementById(
  "cancelResetPasswordButton"
);

function showMessage(text, type) {
  messageDisplay.textContent = text;
  messageDisplay.classList.remove(
    "hidden",
    "bg-green-100",
    "text-green-800",
    "bg-red-100",
    "text-red-800",
    "bg-blue-100",
    "text-blue-800"
  );

  if (type === "success") {
    messageDisplay.classList.add("bg-green-100", "text-green-800");
  } else if (type === "error") {
    messageDisplay.classList.add("bg-red-100", "text-red-800");
  } else {
    messageDisplay.classList.add("bg-blue-100", "text-blue-800"); // Default to info styling
  }

  setTimeout(() => {
    messageDisplay.classList.add("hidden");
  }, 5000);
}

/**
 * Checks if a user is currently authenticated by verifying the presence of JWT tokens in localStorage.
 * @returns {boolean} True if both accessToken and refreshToken are found, false otherwise.
 */
function isAuthenticated() {
  return (
    localStorage.getItem("accessToken") !== null &&
    localStorage.getItem("refreshToken") !== null
  );
}

/**
 * Toggles the visibility of the authentication section (login/register) and the main application section.
 * If a user is authenticated, the app section is shown; otherwise, the auth section is shown.
 */
function updateUIVisibility() {
  if (isAuthenticated()) {
    authSection.classList.add("hidden"); // Hide login/register
    appSection.classList.remove("hidden"); // Show app dashboard
    fetchUserProducts(); // <--- Call fetchUserProducts when app section is shown
  } else {
    authSection.classList.remove("hidden"); // Show login/register
    appSection.classList.add("hidden"); // Hide app dashboard
  }
}

/**
 * Clears authentication tokens from localStorage, updates the UI to show the login form,
 * and resets relevant dashboard elements.
 */
function logout() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("userEmail"); // Also clear stored email
  showMessage("You have been logged out.", "info");
  updateUIVisibility(); // Switch to showing the login form
  welcomeMessage.textContent = "Welcome, shopper!"; // Reset welcome message
  productsTableBody.innerHTML = ""; // Clear displayed products
  productsTable.classList.add("hidden"); // Hide the table
  noProductsMessage.classList.remove("hidden"); // Show "no products" message
}

/**
 * Fetches the price tracking requests for the currently authenticated user from the backend
 * and then renders them into the dashboard table.
 */
async function fetchUserProducts() {
  console.log("fetchUserProducts called."); // Debugging log
  // If not authenticated (e.g., token manually removed), perform logout
  if (!isAuthenticated()) {
    logout();
    console.log("fetchUserProducts: User not authenticated, logging out."); // Debugging log
    return;
  }

  const accessToken = localStorage.getItem("accessToken");
  const userEmail = localStorage.getItem("userEmail"); // Retrieve user email
  if (userEmail) {
    welcomeMessage.textContent = `Welcome, ${userEmail}!`; // Set welcome message with email
  } else {
    welcomeMessage.textContent = "Welcome, shopper!";
  }

  showMessage("Loading your tracked products...", "info");
  console.log(
    "fetchUserProducts: Attempting to fetch from /app/myRequests with token:",
    accessToken ? "present" : "missing"
  ); // Debugging log

  try {
    // Call the correct dashboard endpoint
    const response = await fetch(
      `${API_BASE_URL}/app/myRequests`, // Corrected endpoint URL
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`, // Attach JWT for authentication
        },
      }
    );

    if (response.ok) {
      // Try to parse as JSON first
      let products;
      try {
        products = await response.json();
      } catch (jsonError) {
        // If JSON parsing fails, get raw text and log it
        const rawText = await response.text();
        console.error(
          "fetchUserProducts: JSON parsing failed. Raw response:",
          rawText,
          jsonError
        );
        showMessage("Error: Received invalid data from server.", "error");
        return; // Stop execution if data is invalid
      }

      console.log(
        "fetchUserProducts: Successfully fetched products:",
        products
      ); // Debugging log
      renderProducts(products); // Render the fetched products
      // Removed: showMessage("Your tracked products loaded successfully!", "success");
    } else if (response.status === 204) {
      // HTTP 204 No Content for empty lists
      console.log("fetchUserProducts: No products found (204 No Content)."); // Debugging log
      renderProducts([]); // Render an empty list
      showMessage("No products are being tracked yet.", "info");
    } else {
      const errorText = await response.text();
      console.error(
        "fetchUserProducts: Error fetching products:",
        response.status,
        errorText
      ); // Debugging log
      showMessage(
        `Error fetching products: ${response.status} - ${
          errorText || "Something went wrong."
        }`,
        "error"
      );
    }
  } catch (error) {
    console.error(
      "fetchUserProducts: Network or server error fetching products:",
      error
    ); // Debugging log
    // Removed: showMessage("Failed to connect to the server to fetch products. Please check your backend.", "error");
  }
}

/**
 * Renders a list of price tracking product objects into the HTML table.
 * @param {Array<Object>} products - An array of product objects (UserDashboardProductDto) to display.
 */
function renderProducts(products) {
  productsTableBody.innerHTML = ""; // Clear any existing rows in the table body

  if (products.length === 0) {
    productsTable.classList.add("hidden"); // Hide the table
    noProductsMessage.classList.remove("hidden"); // Show "no products" message
  } else {
    productsTable.classList.remove("hidden"); // Show the table
    noProductsMessage.classList.add("hidden"); // Hide "no products" message

    products.forEach((product) => {
      // ADDED DEBUG LOG HERE to inspect the product object
      console.log("Rendering product:", product);

      const row = productsTableBody.insertRow(); // Insert a new row for each product
      row.classList.add("hover:bg-gray-50"); // Add hover effect
      // NEW: Add data-request-id attribute to the row for easy targeting
      row.dataset.requestId = product.priceTrackingRequestId;

      // Product Name Cell
      const productNameCell = row.insertCell();
      productNameCell.classList.add(
        "px-6",
        "py-4",
        "whitespace-nowrap",
        "text-sm",
        "font-medium",
        "text-gray-900"
      );
      productNameCell.textContent = product.productName || "N/A"; // Display product name

      // Product Number Cell
      const productNumberCell = row.insertCell();
      productNumberCell.classList.add(
        "px-6",
        "py-4",
        "whitespace-nowrap",
        "text-sm",
        "font-medium",
        "text-gray-900"
      );
      productNumberCell.textContent = product.productNumber;

      // Max Price Cell (formatted to 2 decimal places)
      const maxPriceCell = row.insertCell();
      maxPriceCell.classList.add(
        "px-6",
        "py-4",
        "whitespace-nowrap",
        "text-sm",
        "text-gray-500"
      );
      maxPriceCell.textContent = `$${
        product.maxPrice ? product.maxPrice.toFixed(2) : "N/A"
      }`;

      // Actions Cell (Delete Button)
      const actionsCell = row.insertCell();
      actionsCell.classList.add(
        "px-6",
        "py-4",
        "whitespace-nowrap",
        "text-right",
        "text-sm",
        "font-medium",
        "actions-cell"
      ); // Added actions-cell class for custom CSS

      const deleteButton = document.createElement("button");
      deleteButton.textContent = "Delete";
      deleteButton.classList.add(
        "text-red-600",
        "hover:text-red-900",
        "ml-4",
        "focus:outline-none",
        "focus:ring-2",
        "focus:ring-offset-2",
        "focus:ring-red-500",
        "rounded-md",
        "transition",
        "duration-150",
        "ease-in-out",
        "px-3",
        "py-1.5"
      );
      // FIX: Changed product.id to product.priceTrackingRequestId
      deleteButton.onclick = () => {
        console.log(
          "Delete button clicked for product ID:",
          product.priceTrackingRequestId
        );
        showConfirmationModal(product.priceTrackingRequestId); // Pass the correct product ID
      };
      actionsCell.appendChild(deleteButton);
    });
  }
}

// --- Modal Functions ---
function showConfirmationModal(id) {
  console.log("showConfirmationModal called with ID:", id); // ADDED DEBUG LOG
  currentRequestIdToDelete = id;
  confirmationModal.classList.remove("hidden");
}

function hideConfirmationModal() {
  console.log("hideConfirmationModal called."); // ADDED DEBUG LOG
  confirmationModal.classList.add("hidden");
  currentRequestIdToDelete = null;
}

// --- Forgot Password Functions ---
function showForgotPasswordModal() {
  forgotPasswordModal.classList.remove("hidden");
  forgotPasswordEmailInput.value = ""; // Clear previous email
  forgotPasswordEmailInput.focus(); // Focus on the email input
}

function hideForgotPasswordModal() {
  forgotPasswordModal.classList.add("hidden");
}

/**
 * Handles the submission of the forgot password form.
 * Sends a request to the backend to initiate password reset.
 */
async function handleForgotPasswordRequest(event) {
  event.preventDefault();

  sendResetLinkButton.disabled = true;
  sendResetLinkButton.textContent = "Sending...";
  sendResetLinkButton.classList.add("opacity-70", "cursor-not-allowed");

  const email = forgotPasswordEmailInput.value.trim();

  if (!email) {
    showMessage("Please enter your email address.", "error");
    sendResetLinkButton.disabled = false;
    sendResetLinkButton.textContent = "Send Reset Link";
    sendResetLinkButton.classList.remove("opacity-70", "cursor-not-allowed");
    return;
  }

  try {
    // This endpoint will be created in the backend (AuthService/AuthController)
    const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    });

    if (response.ok) {
      showMessage(
        "If an account with that email exists, a password reset link has been sent.",
        "success"
      );
      hideForgotPasswordModal();
    } else {
      const errorText = await response.text();
      console.error("Forgot password error:", errorText); // Provide a generic message for security reasons, even if email not found
      showMessage(
        "If an account with that email exists, a password reset link has been sent.",
        "info"
      );
    }
  } catch (error) {
    console.error("Network or server error during forgot password:", error);
    showMessage("Failed to connect to the server. Please try again.", "error");
  } finally {
    sendResetLinkButton.disabled = false;
    sendResetLinkButton.textContent = "Send Reset Link";
    sendResetLinkButton.classList.remove("opacity-70", "cursor-not-allowed");
  }
}

/**
 * Handles the deletion of a specific price tracking request.
 * This function is called after the user confirms in the modal.
 */
async function confirmDeleteRequest() {
  console.log("confirmDeleteRequest called."); // ADDED DEBUG LOG

  const requestId = currentRequestIdToDelete; // Get the ID *before* hiding the modal
  console.log("Attempting to delete request with ID:", requestId); // ADDED DEBUG LOG

  hideConfirmationModal(); // Hide the modal immediately *after* getting the ID

  if (!requestId) {
    showMessage("No request selected for deletion.", "error");
    console.error("confirmDeleteRequest: requestId is null or undefined."); // ADDED DEBUG LOG
    return;
  }

  const accessToken = localStorage.getItem("accessToken");
  if (!accessToken) {
    showMessage("You are not logged in.", "error");
    logout();
    console.error("confirmDeleteRequest: Access token missing."); // ADDED DEBUG LOG
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/app/delete/${requestId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (response.ok) {
      showMessage("Price tracking request deleted successfully!", "success");
      // NEW: Immediately remove the row from the DOM
      const rowToRemove = document.querySelector(
        `tr[data-request-id="${requestId}"]`
      );
      if (rowToRemove) {
        rowToRemove.remove();
        console.log(`Removed row for request ID ${requestId} from DOM.`);
      }

      // Check if the table is now empty and update visibility
      if (productsTableBody.children.length === 0) {
        productsTable.classList.add("hidden");
        noProductsMessage.classList.remove("hidden");
        console.log("No more tracked products, showing empty message.");
      }

      // No need to call fetchUserProducts() immediately after removing the row.
      // The list is already visually updated. fetchUserProducts() can be called
      // on page load or when navigating back to this section.
      // If you still want to re-fetch for full data consistency, you can keep it,
      // but the immediate DOM removal is the main fix for the double-click issue.
      // fetchUserProducts(); // Keep this if you want to ensure full data consistency, but it's not strictly necessary for the immediate UI update.
    } else if (response.status === 401 || response.status === 403) {
      showMessage(
        "Session expired or unauthorized. Please log in again.",
        "error"
      );
      logout();
    } else {
      const errorText = await response.text();
      console.error("Delete error:", response.status, errorText); // Log status and text
      showMessage(
        `Failed to delete request: ${errorText || "Something went wrong."}`,
        "error"
      );
    }
  } catch (error) {
    console.error("Network error deleting request:", error); // Log the actual error object
    showMessage(
      "Failed to connect to the server to delete request. Please check your backend.",
      "error"
    );
  }
}

// --- Event Listeners ---

// Event listener for Login Form submission
loginForm.addEventListener("submit", async (event) => {
  event.preventDefault(); // Prevent default browser form submission

  // Disable button and show loading state
  loginButton.disabled = true;
  loginButton.textContent = "Logging in...";
  loginButton.classList.add("opacity-70", "cursor-not-allowed");

  const email = loginEmailInput.value.trim();
  const password = loginPasswordInput.value.trim();

  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      const result = await response.json(); // Store tokens and user email in localStorage
      localStorage.setItem("accessToken", result.accessToken);
      localStorage.setItem("refreshToken", result.refreshToken);
      localStorage.setItem("userEmail", email); // Still store email, just not displaying it in welcome
      showMessage("Login successful!", "success");
      loginForm.reset(); // Clear form fields
      updateUIVisibility(); // Switch to app section
      // fetchUserProducts() is called inside updateUIVisibility()
    } else {
      // Handle API error response (e.g., invalid credentials)
      const errorData = await response
        .json()
        .catch(() => ({ message: "Unknown error" })); // Try to parse JSON, fallback to generic
      const errorMessage =
        errorData.message || "Login failed. Invalid credentials.";
      showMessage(`Login failed: ${errorMessage}`, "error");
      console.error("Login error:", errorData);
    }
  } catch (error) {
    // Handle network or other unexpected errors
    console.error("Network or server error during login:", error);
    showMessage("Failed to connect to the server. Please try again.", "error");
  } finally {
    // Re-enable button and restore original text
    loginButton.disabled = false;
    loginButton.textContent = "Login";
    loginButton.classList.remove("opacity-70", "cursor-not-allowed");
  }
});

// Event listener for Register Form submission
registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  registerButton.disabled = true;
  registerButton.textContent = "Registering...";
  registerButton.classList.add("opacity-70", "cursor-not-allowed");

  const email = registerEmailInput.value.trim();
  const password = registerPasswordInput.value.trim();

  try {
    // This assumes a /api/auth/register endpoint exists in your backend
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      showMessage("Registration successful! You can now log in.", "success");
      registerForm.reset(); // Clear form fields
      loginTab.click(); // Automatically switch to the login tab
    } else {
      const errorText = await response.text();
      console.error("Registration error:", errorText);
      showMessage(
        `Registration failed: ${response.status} - ${
          errorText || "Something went wrong."
        }`,
        "error"
      );
    }
  } catch (error) {
    console.error("Network or server error during registration:", error);
    showMessage("Failed to connect to the server. Please try again.", "error");
  } finally {
    registerButton.disabled = false;
    registerButton.textContent = "Register";
    registerButton.classList.remove("opacity-70", "cursor-not-allowed");
  }
});

// Event listener for Logout Button click
logoutButton.addEventListener("click", logout);

// Event listener for "Search for Products" button click
goToSearchButton.addEventListener("click", () => {
  console.log("Attempting to redirect to /search"); // ADD THIS DEBUG LOG
  // This log will confirm if the event listener is firing.
  window.location.href = "/search"; // Redirect to the React app served at /search
});

// Event listeners for tab switching between Login and Register forms
loginTab.addEventListener("click", () => {
  loginTab.classList.add("active");
  registerTab.classList.remove("active");
  loginForm.classList.remove("hidden");
  registerForm.classList.add("hidden");
  messageDisplay.classList.add("hidden"); // Hide any active messages
});

registerTab.addEventListener("click", () => {
  registerTab.classList.add("active");
  loginTab.classList.remove("active");
  registerForm.classList.remove("hidden");
  loginForm.classList.add("hidden");
  messageDisplay.classList.add("hidden"); // Hide any active messages
});

// Event listeners for the custom confirmation modal
confirmDeleteButton.addEventListener("click", confirmDeleteRequest);
cancelDeleteButton.addEventListener("click", hideConfirmationModal);

// Event listeners for Forgot Password
forgotPasswordLink.addEventListener("click", (event) => {
  event.preventDefault(); // Prevent default link behavior
  showForgotPasswordModal();
});
forgotPasswordForm.addEventListener("submit", handleForgotPasswordRequest);
cancelResetPasswordButton.addEventListener("click", hideForgotPasswordModal);

// --- Initial Application Load Logic ---
// This runs once the DOM is fully loaded.
document.addEventListener("DOMContentLoaded", () => {
  updateUIVisibility(); // Set initial UI based on authentication status
  // fetchUserProducts() is now called inside updateUIVisibility()
});
