import { PrimeIcons } from "primereact/api"
import { Button } from "primereact/button"
import "./ComponentListHeader.css";

const ComponentListHeader = ({ label, addAction }: { label: string, addAction?: Function }) => {

    const add = (e: any) => {
        if (addAction) {
            addAction();
        }

        e.stopPropagation();
    };

    return (
        <div className="component-list-header">
            <span className="vertical-align-middle">{label}</span>
            <div className="tools">
                {
                    addAction
                        ? (
                            <Button onClick={add}>
                                <i className={PrimeIcons.PLUS} />
                            </Button>
                        )
                        : <></>
                }
            </div>
        </div>
    )
};

export default ComponentListHeader;