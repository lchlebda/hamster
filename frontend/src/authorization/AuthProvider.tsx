import React from 'react';

interface AuthContextType {
    user: any;
    token: string;
    signIn: (user: string, callback: VoidFunction) => void;
    signOut: (callback: VoidFunction) => void;
}

const AuthContext = React.createContext<AuthContextType>(null!);

const fakeAuthProvider = {
    isAuthenticated: false,
    signIn(callback: VoidFunction): string {
        fakeAuthProvider.isAuthenticated = true;
        setTimeout(callback, 100); // fake async
        return 'abcdef';
    },
    signOut(callback: VoidFunction) {
        fakeAuthProvider.isAuthenticated = false;
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
        const token = fakeAuthProvider.signIn(() => {
            setUser(newUser);
            callback();
        });
        setToken(token);
    };

    const signOut = (callback: VoidFunction) => {
        return fakeAuthProvider.signOut(() => {
            setUser(null);
            callback();
        });
    };

    const value = { user, token, signIn, signOut };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export default AuthProvider;