import { Link } from "react-router-dom";

export default function VideoCard({ video }) {
  return (
    <div className="border rounded p-3 shadow-sm">
      <Link to={`/video/${video.id || video.videoId}`}>
        <div className="h-40 bg-gray-200 flex items-center justify-center">
          <span>{video.title || "No title"}</span>
        </div>
      </Link>
      <div className="mt-2">
        <h3 className="font-semibold">{video.title}</h3>
        <p className="text-sm text-gray-600">{video.description}</p>
      </div>
    </div>
  );
}
