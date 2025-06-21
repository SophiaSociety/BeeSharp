// Authentication utilities
export interface User {
    username: string;
    token: string;
}

export function getAuthToken(): string | null {
    if (typeof window !== 'undefined') {
        return localStorage.getItem('authToken');
    }
    return null;
}

export function getUsername(): string | null {
    if (typeof window !== 'undefined') {
        return localStorage.getItem('username');
    }
    return null;
}

export function isAuthenticated(): boolean {
    return getAuthToken() !== null;
}

export function logout(): void {
    if (typeof window !== 'undefined') {
        localStorage.removeItem('authToken');
        localStorage.removeItem('username');
        window.location.href = '/';
    }
}

export function getAuthHeaders(): Record<string, string> {
    const token = getAuthToken();
    return token ? {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    } : {
        'Content-Type': 'application/json'
    };
}

// Function to make authenticated API requests
export async function authenticatedFetch(url: string, options: RequestInit = {}): Promise<Response> {
    const headers = {
        ...getAuthHeaders(),
        ...options.headers
    };

    const response = await fetch(url, {
        ...options,
        headers
    });

    // If unauthorized, logout and redirect
    if (response.status === 401) {
        logout();
    }

    return response;
}
