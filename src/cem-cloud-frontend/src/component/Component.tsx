import { useParams } from "react-router-dom";

const Component = () => {

    const params = useParams();

    return (
        <>
        <p>{params.componentId}</p>
        </>
    )
};

export default Component;