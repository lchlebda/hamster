import React from 'react';

interface AuthContextType {
    user: any;
    token: string;
    signIn: (user: string, callback: VoidFunction) => void;
    signOut: (callback: VoidFunction) => void;
}

const AuthContext = React.createContext<AuthContextType>(null!);

const StravaAuthProvider = {
    isAuthenticated: false,
    signIn(callback: VoidFunction): string {
        StravaAuthProvider.isAuthenticated = true;
        setTimeout(callback, 100); // fake async
        return 'abcdef';
    },
    signOut(callback: VoidFunction) {
        StravaAuthProvider.isAuthenticated = false;
        setTimeout(callback, 100);
    },
};

export const useAuth = () => {
    return React.useContext(AuthContext);
}

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = React.useState<any>(null);
    const [token, setToken] = React.useState<any>(null);

    const signIn = (newUser: string, callback: VoidFunction) => {
        const token = StravaAuthProvider.signIn(() => {
            setUser(newUser);
            callback();
        });
        setToken(token);
    };

    const signOut = (callback: VoidFunction) => {
        return StravaAuthProvider.signOut(() => {
            setUser(null);
            callback();
        });
    };

    const value = { user, token, signIn, signOut };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export default AuthProvider;