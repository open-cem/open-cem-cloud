import { useParams } from "react-router-dom";

const Installation = () => {
    const params = useParams<string>();

    return (
        <>
            <h1>Installation {params.installationId}</h1>
        </>
    );
};

export default Installation;