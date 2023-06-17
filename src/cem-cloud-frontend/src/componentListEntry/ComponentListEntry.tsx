import { PrimeIcons } from "primereact/api";
import { Button } from "primereact/button";
import "./ComponentListEntry.css";

const ComponentListEntry = ({ label }: { label: string }) => {
    return (
        <div className="component-list-entry container">
            <Button>{label}</Button>
            <Button severity="danger"><i className={PrimeIcons.TRASH} /></Button>
        </div>
    )
};

export default ComponentListEntry;